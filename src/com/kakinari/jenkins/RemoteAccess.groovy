package com.kakinari.jenkins

class RemoteAccess  implements Serializable {
    def RemoteUser info
    def staps

    RemoteAccess(steps, info) {
        this.steps = steps
         this.info = info
    }

    def String remsh(String command) {
        steps.sshagent([ info.credential ]) {
            return steps.sh(
                script : "ssh ${info.username}@${info.hostname} ${command}",
                returnStdout: true)
        }    
    }

    def int remshStatus(String command) {
        steps.sshagent([ info.credential ]) {
            return steps.sh(
                script : "ssh ${info.username}@${info.hostname} ${command}",
                returnStatus: true)
        }    
    }

    def boolean getFile(String from, String to = '.', opts = '') {
        steps.sshagent([ info.credential ]) {
            return (steps.sh(
                script : "scp ${opts} ${info.username}@${info.hostname}:${from} ${to}",
                returnStatus: true) == 0)
        }    
    }

    def boolean putFile( String from, String to = '.', opts = '') {
        steps.sshagent([ info.credential ]) {
            return (steps.sh(
                script : "scp ${opts} ${from} ${info.username}@${info.hostname}:${to}",
                returnStatus: true) == 0)
        }    
    }

    def boolean removeFile(String name) {
        return (remshStatus(steps, "rm -f ${name}") == 0)
    }

    def boolean isFileExist(String name) {
        return (remshStatus(steps, "ls ${name} >/dev/null 2>&1") == 0)
    }
}
