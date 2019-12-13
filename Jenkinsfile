pipeline {
  agent any
  stages {
    stage('User test') {
      steps {
        script {
          RemoteAccessDevApi().remsh(steps, "uname -a")
        }
      }

    }
  }
}