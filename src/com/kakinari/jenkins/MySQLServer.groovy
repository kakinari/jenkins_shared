package com.kakinari.jenkins

import java.io.File
import java.io.BufferedWriter

class MySQLServer implements Serializable {
    def steps
    def volume
    def port
    def name
    def image
    def password
    def schema

    MySQLServer(steps, info) {
        this.steps = steps
        this.port = info?.get('port') ?: null
        this.volume = info?.get('volume') ?: null
        this.name = info?.get('name') ?: "sql_server"
        this.image = info?.get('image') ?: "kakinari/mysql-ja:Server-5.7"
        this.password = info?.get('password') ?: 'T3lsys.1181'
        this.schema = info?.get('schema') ?: ""
    }

    def start() {
        steps.sh "docker run --name ${name} ${control()} ${image}"
        steps.sh "docker exec ${name} /root/setRootPassword ${password}"
    }   

    def stop() {
        steps.sh "docker stop ${name}"
        steps.sh "docker image rm -f ${image}"
        if (volume != null)
            steps.sh "docker volume rm -f  ${volume}"
        steps.sh "docker system prune -f"
    }

    def Dump(String file) {
        steps.sh(script: "docker exec ${name} mysqldump --quick --single-transaction ${schema} | gzip > ${file}")
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

    def execute(String query) {
        String tmpfile = '/var/tmp/execquery.query'
        storeFile(tmpfile, query)
        executeQuery(tmpfile, schema)
        deleteFile(tmpfile)
    }

    def executeQuery(String filename) {
        String database = schema?: ""
        if (filename.endsWith('gz'))
            steps.sh(script: "zcat ${filename} | docker exec -i ${name} mysql ${database}")
        else
            steps.sh(script: "cat ${filename} | docker exec -i ${name} mysql ${database}")
    }
}