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
//        environment {
//            NEXUS = credentials('NEXUS')
//        }

        stages {

            stage('Code Quality') {
                steps {
                    sh 'ls -l'
 //                   sh 'sonar-scanner -Dsonar.projectKey=${component} -Dsonar.host.url=http://172.31.80.236:9000 -Dsonar.login=admin -Dsonar.password=admin123 -Dsonar.qualitygate.wait=true'
                }
            }

            stage('Unit Test Cases') {
                steps {
                    sh 'echo Unit tests'
                    sh 'python3.6 -m unittest'
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
                    sh 'echo $TAG_NAME >VERSION'
                  //  sh 'zip -r ${component}-${TAG_NAME}.zip *.ini *.py *.txt VERSION ${schema_dir}'
                  //  sh 'curl -f -v -u ${NEXUS_USR}:${NEXUS_PSW} --upload-file ${component}-${TAG_NAME}.zip http://3.91.175.12:8081/repository/${component}/${component}-${TAG_NAME}.zip'
                    sh 'aws ecr get-login-password --region us-east-1 | docker login --username AWS --password-stdin 193400300103.dkr.ecr.us-east-1.amazonaws.com'
                    sh 'docker build -t 193400300103.dkr.ecr.us-east-1.amazonaws.com/${component}:${TAG_NAME} .'
                    sh 'docker push 193400300103.dkr.ecr.us-east-1.amazonaws.com/${component}:${TAG_NAME}'
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