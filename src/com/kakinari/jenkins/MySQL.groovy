package com.kakinari.jenkins

class MySQL implements Serializable {
    def steps
    def container
    def hostname
    def username
    def password
    def schema

    MySQL(steps, info) {
        this.steps = steps
        this.container = info?.get('container') ?: null
        this.hostname = info?.get('hostname') ?: null
        this.username = info?.get('username') ?: null
        this.password = info?.get('password') ?: null
        this.schema    = info?.get('schema') ?: null
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


}