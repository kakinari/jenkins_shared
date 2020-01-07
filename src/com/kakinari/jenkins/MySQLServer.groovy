package com.kakinari.jenkins

class MySQLServer implements Serializable {
    def steps
    def volume = null
    def port = null
    def name = "sql_server"
    def image = "kakinari/mysql-ja:Server-5.7"

    MySQLServer(steps, info) {
        this.steps = steps
        if (info?.port != null)
            this.port = info.port
        if (info?.volume != null)
            this.volume = info.volume
        if (info?.name != null)
            this.name = info.name
        if (info?.image != null)
            this.image = info.image
    }

    def start() {
        steps.sh "docker run --name ${name} ${control()} ${image}"
        steps.sh "docker exec ${name} /root/setRootPassword ${password}"
    }   

    def stop() {
        step.sh "docker stop ${name}"
        step.sh "docker rm -f ${name}"
        step.sh "docker image rm -f ${image}"
        if (volume != null)
            step.sh "docker volume rm -f  ${volume}"
        step.sh "docker system prune -f"
    }

    def control() {
        retstr = ""
        if (port != null)
            retstr += "-e ${port}:3306 "
        if (volume != null)
            retstr += "-v ${volume}:/var/lib/mysql "
        return "--privileged -d --rm " + restr
    }
}