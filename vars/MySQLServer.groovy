import com.kakinari.jenkins.MySQLServerInfo

def start(steps, MySQLServerInfo info) {
    steps.sh "docker run --name ${info.name} ${control(info)} ${info.image}"
    steps.sh "docker exec ${info.name} /root/setRootPassword ${info.password}"
}   

def stop(steps, MySQLServerInfo info) {
    steps.sh "docker stop ${name}"
    steps.sh "docker image rm -f ${image}"
    if (volume != null)
        steps.sh "docker volume rm -f  ${volume}"
    steps.sh "docker system prune -f"
}

def control(MySQLServerInfo info) {
    def retstr = ""
    if (info.port != null)
        retstr += "-e ${info.port}:3306 "
    if (info.volume != null)
        retstr += "-v ${info.volume}:/var/lib/mysql "
    return "--privileged -d --rm " + retstr
}

def execute(steps, MySQLServerInfo info, query) {
    steps.writeFile(
        file: "${info.tmpfile}",
        text: "mysql <<EOF\n${query}\nEOF".replace("`", "\\`")
    )
    steps.sh(script: "docker cp ${info.tmpfile} ${info.name}:/root/${info.tmpfile};docker exec ${info.name} sh /root/${info.tmpfile}; docker exec ${info.name} rm /root/${info.tmpfile}")
}