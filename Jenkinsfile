pipeline {
  agent any
  stages {
    stage('User test') {
      steps {
        script {
          DevApiUser user = DevApiUser()
          def cred = [ user.credential ]
          sshagent(cred) {
            sh 'ssh -o StrictHostKeyChecking=no -l root ${user.host} uname -a'
          }
        }
      }
    }

  }
}