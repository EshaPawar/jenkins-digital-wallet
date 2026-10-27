
pipeline {

    agent any

    parameters {
        string(
            name: 'TOMCAT_WEBAPPS',
            defaultValue: 'D:\\Tomcat\\apache-tomcat-10.1.61\\webapps',
            description: 'Tomcat webapps directory for deployment'
        )
    }

    environment {
        JAVA_HOME = tool 'PyCharm-Java21'
        MAVEN_HOME = 'D:\\Maven\\apache-maven-3.10.0'
        PATH = "${JAVA_HOME}\\bin;${MAVEN_HOME}\\bin;${PATH}"
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
                bat """
                    copy /Y "target\\digital-wallet.war" "%TOMCAT_WEBAPPS%\\digital-wallet.war"
                """
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
