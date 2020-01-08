package com.kakinari.jenkins

class MySQLServer implements Serializable {
    def steps
    def volume
    def port
    def name
    def image
    def password

    MySQLServer(steps, info) {
        this.steps = steps
        this.port = info?.get('port') ?: null
        this.volume = info?.get('volume') ?: null
        this.name = info?.get('name') ?: "sql_server"
        this.image = info?.get('image') ?: "kakinari/mysql-ja:Server-5.7"
        this.password = info?.get('password') ?: 'T3lsys.1181'
    }

    def start() {
        steps.sh "docker run --name ${name} ${control()} ${image}"
        steps.sh "docker exec ${name} /root/setRootPassword ${password}"
    }   

    def stop() {
        steps.sh "docker stop ${name}"
//        steps.sh "docker rm -f ${name}"
        steps.sh "docker image rm -f ${image}"
        if (volume != null)
            steps.sh "docker volume rm -f  ${volume}"
        steps.sh "docker system prune -f"
    }

    def control() {
        def retstr = ""
        if (this.port != null)
            retstr += "-e ${this.port}:3306 "
        if (this.volume != null)
            retstr += "-v ${this.volume}:/var/lib/mysql "
        return "--privileged -d --rm " + retstr
    }

    String getName() {
        return this.name
    }

    String getVolumr() {
        return this.volume
    }

    def execute(query) {
        def tmpfile = 'execquery.query'
        steps.writeFile(
            file: "${tmpfile}",
            text: "mysql <<EOF\n${query}\nEOF"
        )
        steps.sh(script: "docker cp ${tmpfile} ${name}:${tmpfile};docker exec ${name} sh ${tmpfile}; docker exec ${name} rm ${tmpfile}")
    }
}