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
        this.template    = info?.get('template') ?: "com/kakinari/jenkins/queries"
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

    def getTemplate(String file) {
        return libraryResource "${template}/${file}"
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

}