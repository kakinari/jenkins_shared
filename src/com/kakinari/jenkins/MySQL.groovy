package com.kakinari.jenkins

class MySQL implements Serializable {
    def steps
    def container
    def hostname
    def username
    def password
    def schema
    def template

    MySQL(steps, info) {
        this.steps = steps
        this.container = info?.get('container') ?: null
        this.hostname = info?.get('hostname') ?: null
        this.username = info?.get('username') ?: null
        this.password = info?.get('password') ?: null
        this.schema    = info?.get('schema') ?: null
        this.template  = info?.get('template') ?: "com/kakinari/jenkins/queries"
    }

    def commandLine(cmd = "mysql") {
        def command = ""
        if (container != null)
            command += "docker exec -i ${container} "
        command += "${cmd} "
        if (hostname != null)
            command += "--host=${hostname} "
        if (username != null)
            command += "--user=${username} "
        if (password != null)
            command += "--password=${password} "
        if (schema != null)
            command += "${schema} "
        return command
    }

    @NonCPS
    def storeFile(filename, query) {
        new File(filename).withWriter('utf-8') { BufferedWriter writer ->
            query.split("\n").each {
                writer.writeLine it
            }
        }
    }

    def deleteFile(filename) {
        new File(filename).delete()
    }

    def getTemplate(file) {
        return steps.libraryResource("${template}/${file}")
    }

    def execute(String query) {
        String tmpfile = '/var/tmp/execquery.query'
        storeFile(tmpfile, query)
        executeQuery(tmpfile)
        deleteFile(tmpfile)
    }

    def executeQuery(String filename) {
        if (filename.endsWith('gz'))
            steps.sh(script: "zcat ${filename} | ${commandLine()}")
        else
            steps.sh(script: "cat ${filename} |  ${commandLine()}")
    }

	def getquery(String filename, String colname = null, String data = null, String extra = null, String ordercond = null, String groupcond = null) {
        if (filename == null)
            return ""
        return "${getTemplate(filename)}${getCondition(colname, data, extra, ordercond, groupcond)}"
    }

	def getCondition(String colname, String data, String extra = null, String ordercond = null, String groupcond = null) {
        if (colname == null || data == null)
            return ""
		String conn = " WHERE ";
		StringBuffer buff = new StringBuffer();
		if (extra != null) {
			buff.append("${conn} ( ${extra} )");
			conn =  "\n AND (";
		}
		buff.append(getConditionString(colname, data, conn));
		if (extra != null)
			buff.append(")");
		if (groupcond != null)
			buff.append("\n GROUP BY ").append(groupcond);
		if (ordercond != null)
			buff.append("\n ORDER BY ").append(ordercond);
		buff.append(";");
		return buff.toString();
	}

      def getConditionString(String colname, String data, String conn = "") {
        if (colname == null || data == null)
            return "";
        StringBuffer buff = new StringBuffer();
        if (!colname.contains("`") && !colname.contains("("))
            colname = "`${colname}`";
        for (String row :data.split("\n")) {
            if (row == null || row.length() == 0)
                continue;
            if (row.contains("&")) {
                buff.append(conn);
                String conn2 = " ((";
                for (String part : row.split("&")) {
                    buff.append("${conn2} ${colname} ${getConditionString(part)}");
                    conn2 = ") \n  AND (";
                }
                buff.append("))");
            } else {
                buff.append("${conn} ${colname} ${getConditionString(row)}");
            }
            conn = "\n OR ";
        }
        return buff;
    }

	def  getConditionString(String row) {
        if (row.contains("-")) {
            String[] keyval = row.split("-",2);
            return " BETWEEN \'${keyval[0]}\' AND \'${keyval[1]}\'"
        } else if (row.contains(",")) {
            StringBuffer buff = new StringBuffer();
            buff.append(" IN (");
            String head = ""
            row.split(",").each {
                buff.append("${head} '${it}'")
                head = ", "
            }
            buff.append(")")
            return buff.toString()
        } else if (row.startsWith("~")) {
            return " REGEXP \'${row.substring(1)}\'"
        } else if (row.startsWith("!~")) {
            return " NOT REGEXP \'${row.substring(2)}\'"
        } else if (row.startsWith("=")) {
            return " = \'${row.substring(1)}\'"
        } else if (row.startsWith("<>")) {
            return " <> \'${row.substring(2)}\'"
        } else if (row.startsWith("<")) {
            return " < \'${row.substring(1)}\'"
        } else if (row.startsWith(">")) {
            return " > \'${row.substring(1)}\'"
        } else if (row.equals("NULL")) {
            return " IS NULL"
        } else if (row.equals("!NULL")) {
            return " IS NOT NULL"
        } else if (row.contains("%")) {
            if (row.contains("_")) {
                return " LIKE \'${row.replace('_', '\$_')}\' ESCAPE '\$'"
            } else 
                return " LIKE \'${row}\'"
        } else {
            if (row.contains("_")) {
                return " LIKE \'${row.replace('_', '\$_')}%\' ESCAPE '\$'"
            } else 
                return " LIKE \'${row}%\'"
        }
    }

}