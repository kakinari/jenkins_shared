import com.kakinari.jenkins.RemoteUser

def call() {
    def user = DevApiUser()
    def cred = [ user.credential ]
    sshagent(cred) {
        sh 'ssh -o StrictHostKeyChecking=no -l root ${user.host} uname -a'
    }
}