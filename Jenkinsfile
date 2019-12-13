pipeline {
  agent any
  stages {
    stage('User test') {
      steps {
        script {
          def result = RemoteAccessDevApi(steps).remsh( "uname -a")
          print result
        }
      }

    }
  }
}