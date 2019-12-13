pipeline {
  agent any
  stages {
    stage('User test') {
      steps {
        script {
          def result = RemoteAccessDevApi().remsh(steps, "uname -a")
          print result
        }
      }

    }
  }
}