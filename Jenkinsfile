pipeline {
  agent any
  stages {
    stage('User test') {
      steps {
        script {
          def cred = {}
          cred.add(DevApiUser())
          sshagent(cred) {
            sh 'ssh -o StrictHostKeyChecking=no -l root ${remote.host} uname -a'
          }
        }
      }
    }

  }
}