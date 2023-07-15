def call() {
    pipeline {

        agent {
            node {
                label 'workstation'
            }
        }

        options {
            ansiColor('xterm')
        }
        environment {
            NEXUS = credentials('NEXUS')
        }

//        parameters {
//            choice(name: 'env', choices: ['dev', 'prod'], description: 'Pick environment')
//         //   choice(name: 'action', choices: ['apply', 'destroy'], description: 'Pick environment')
//        }

        stages {


            stage('Code Quality') {
                steps {
                   sh 'echo Code Quality'
                   sh 'ls -l'
                 //   sh 'sonar-scanner -Dsonar.projectKey=${component} -Dsonar.host.url=http://172.31.80.236:9000 -Dsonar.login=admin -Dsonar.password=admin123'
                }
            }

            stage('Unit Test Cases') {
                steps {
                    sh 'echo Unit tests'
                }
            }

            stage('CheckMarx SAST Scan') {
                steps {
                    sh 'echo Checkmarx Scan'
                }
            }

            stage('CheckMarx SCA Scan') {
                steps {
                    sh 'echo Checkmarx SCA Scan'
                }
            }

            stage('Release Application') {
                when {
                    expression {
                        env.TAG_NAME ==~ ".*"
                    }
                }
                steps {
                    sh 'npm install'
                    sh 'echo $TAG_NAME >VERSION'
                    sh 'zip -r ${component}-${TAG_NAME}.zip node_modules server.js VERSION'
                    sh 'curl -v -u ${NEXUS_USR}:${NEXUS_PSW} --upload-file ${component}-${TAG_NAME}.zip http://172.31.87.83:8081/repository/${component}/${component}-${TAG_NAME}.zip'

                    sh 'echo Release Application'
                }
            }

        }

        post {
            always {
                cleanWs()
            }
        }

    }
}
