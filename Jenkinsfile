pipeline {
    agent any

    tools {
        jdk 'JDK-8'
        maven 'Maven-3.8'
    }

    environment {
        SCANNER_HOME = tool 'sonar-scanner'
    }

    stages {

        stage('Checkout') {
            steps {
                git branch: 'main',
                    url: 'https://github.com/Dhruvpatil56/petclinic.git'
            }
        }

        stage('Build') {
            steps {
                sh 'mvn clean package -P MySQL -DskipTests'
            }
        }

        stage('SonarQube Analysis') {
            steps {
                withSonarQubeEnv('sonar') {
                    sh """
                    ${SCANNER_HOME}/bin/sonar-scanner \
                    -Dsonar.projectKey=petclinic \
                    -Dsonar.projectName=petclinic \
                    -Dsonar.sources=src \
                    -Dsonar.java.binaries=target/classes
                    """
                }
            }
        }

        stage('Quality Gate') {
            steps {
                waitForQualityGate abortPipeline: true,
                                  credentialsId: 'SONAR_TOKEN'
            }
        }

        stage('Docker Build') {
            steps {
                sh 'docker build -t petclinic:latest .'
            }
        }

        stage('Trivy Image Scan') {
            steps {
                sh '''
                trivy image \
                --severity HIGH,CRITICAL \
                --format table \
                -o trivy-image-report.html \
                petclinic:latest
                '''
            }
        }

        stage('Docker Compose Deploy') {
            environment {
                MYSQL_ROOT_PASSWORD = credentials('MYSQL_ROOT_PASSWORD')
                MYSQL_DATABASE      = credentials('MYSQL_DATABASE')
                MYSQL_USER          = credentials('MYSQL_USER')
                MYSQL_PASSWORD      = credentials('MYSQL_PASSWORD')
            }
            steps {
                sh '''
                docker compose down || true
                docker compose up -d --build
                '''
            }
        }
    }

    post {
        success {
            echo '✅ Petclinic deployed successfully'
        }
        failure {
            echo '❌ Pipeline failed'
        }
    }
}
