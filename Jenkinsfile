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

    stage('Stage 2') {
      parallel {
        stage('Stage 2') {
          steps {
            sh 'echo stage 2'
          }
        }

        stage('test2') {
          steps {
            sh 'echo stage2 test 2'
          }
        }

      }
    }

  }
}