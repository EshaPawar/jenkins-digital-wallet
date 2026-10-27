pipeline {

    agent any

    parameters {
        string(
            name: 'TOMCAT_WEBAPPS',
            defaultValue: 'D:\\Tomcat\\apache-tomcat-10.1.61\\webapps',
            description: 'Tomcat webapps directory for deployment'
        )
    }

    stages {

        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build') {
            steps {
                bat 'mvn clean'
            }
        }

        stage('Package') {
            steps {
                bat 'mvn package -DskipTests'
            }
        }

        stage('Deploy') {
            steps {
                echo 'Deploy stage will deploy the WAR file to Tomcat.'
            }
        }
    }

    post {
        success {
            echo 'Digital Wallet pipeline completed successfully.'
        }

        failure {
            echo 'Digital Wallet pipeline failed.'
        }
    }
}