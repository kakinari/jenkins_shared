pipeline {
  agent any
  stages {
    stage('test1') {
      parallel {
        stage('test1') {
          steps {
            sh 'echo test1'
          }
        }

        stage('test 2') {
          steps {
            sh 'echo test 2'
          }
        }

      }
    }

  }
}