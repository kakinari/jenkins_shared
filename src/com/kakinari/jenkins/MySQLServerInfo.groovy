package com.kakinari.jenkins

class MySQLServerInfo implements Serializable {
    def volume
    def port
    def name
    def image
    def password
    def tmpfile

    MySQLServerInfo(Map info) {
        this.port = info?.get('port') ?: null
        this.volume = info?.get('volume') ?: null
        this.name = info?.get('name') ?: "sql_server"
        this.image = info?.get('image') ?: "kakinari/mysql-ja:Server-5.7"
        this.password = info?.get('password') ?: 'T3lsys.1181'
        this.tmpfile = info?.get('tmpfile') ?: 'executeFile.sh'
    }
}