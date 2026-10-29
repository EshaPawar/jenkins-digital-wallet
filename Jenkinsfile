
pipeline {
    agent any

    environment {
        JAVA_HOME = tool 'PyCharm-Java21'
        MAVEN_HOME = 'D:\\Maven\\apache-maven-3.10.0'
        PATH = "${JAVA_HOME}\\bin;${MAVEN_HOME}\\bin;${PATH}"

        IMAGE_NAME = 'divyapawar110806/digital-wallet'
        IMAGE_TAG = "${BUILD_NUMBER}"
        CONTAINER_NAME = 'digital-wallet-container'
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build WAR') {
            steps {
                bat 'mvn clean package -DskipTests'
            }
        }

        stage('Build Docker Image') {
            steps {
                bat 'docker build -t %IMAGE_NAME%:%IMAGE_TAG% .'
                bat 'docker tag %IMAGE_NAME%:%IMAGE_TAG% %IMAGE_NAME%:latest'
                bat 'docker image inspect %IMAGE_NAME%:%IMAGE_TAG%'
            }
        }

        stage('Smoke Test Image') {
            steps {
                script {
                    def candidate = "digital-wallet-candidate-${env.BUILD_NUMBER}"

                    try {
                        bat "docker run -d --name ${candidate} -p 8082:8080 ${env.IMAGE_NAME}:${env.IMAGE_TAG}"

                        powershell """
                            \$ErrorActionPreference = 'Stop'
                            \$ok = \$false

                            for (\$i = 0; \$i -lt 30; \$i++) {
                                try {
                                    \$response = Invoke-WebRequest `
                                        -Uri 'http://localhost:8082/digital-wallet/' `
                                        -TimeoutSec 3 `
                                        -UseBasicParsing

                                    if (\$response.StatusCode -eq 200) {
                                        \$ok = \$true
                                        break
                                    }
                                } catch {
                                    Start-Sleep -Seconds 2
                                }
                            }

                            if (-not \$ok) {
                                docker logs ${candidate}
                                throw 'Docker image smoke test failed.'
                            }

                            Write-Host 'Smoke test passed: HTTP 200'
                        """
                    } finally {
                        bat(
                            script: "docker rm -f ${candidate} 2>NUL",
                            returnStatus: true
                        )
                    }
                }
            }
        }

        stage('Publish to Docker Hub') {
            steps {
                withCredentials([
                    usernamePassword(
                        credentialsId: 'dockerhub-creds',
                        usernameVariable: 'DOCKERHUB_USER',
                        passwordVariable: 'DOCKERHUB_TOKEN'
                    )
                ]) {
                    powershell '''
                        $ErrorActionPreference = 'Stop'

                        $user = $env:DOCKERHUB_USER.Trim()
                        $token = $env:DOCKERHUB_TOKEN.Trim()

                        if ([string]::IsNullOrWhiteSpace($user)) {
                            throw 'Docker Hub username is missing.'
                        }

                        if ([string]::IsNullOrWhiteSpace($token)) {
                            throw 'Docker Hub token is missing.'
                        }

                        Write-Host "Docker Hub username: $user"
                        Write-Host 'Docker Hub token received. Value hidden.'

                        $dockerConfig = Join-Path `
                            $env:TEMP `
                            "jenkins-docker-config-$env:BUILD_NUMBER"

                        New-Item `
                            -ItemType Directory `
                            -Path $dockerConfig `
                            -Force | Out-Null

                        $env:DOCKER_CONFIG = $dockerConfig

                        try {
                            $token | docker login `
                                --username $user `
                                --password-stdin

                            if ($LASTEXITCODE -ne 0) {
                                throw 'Docker Hub login failed. Check the Jenkins credential and Docker login output.'
                            }

                            Write-Host 'Docker Hub login succeeded.'

                            docker push "${env:IMAGE_NAME}:${env:IMAGE_TAG}"

                            if ($LASTEXITCODE -ne 0) {
                                throw 'Versioned image push failed.'
                            }

                            docker push "${env:IMAGE_NAME}:latest"

                            if ($LASTEXITCODE -ne 0) {
                                throw 'Latest image push failed.'
                            }

                            Write-Host 'Both image tags were pushed successfully.'
                        }
                        finally {
                            docker logout

                            if (Test-Path $dockerConfig) {
                                Remove-Item `
                                    -Path $dockerConfig `
                                    -Recurse `
                                    -Force `
                                    -ErrorAction SilentlyContinue
                            }
                        }
                    '''
                }
            }
        }

        stage('Deploy Container') {
            steps {
                powershell """
                    \$ErrorActionPreference = 'Stop'

                    docker rm -f "${env.CONTAINER_NAME}" 2>\$null

                    Write-Host 'Starting fresh application container.'

                    docker run -d `
                        --name "${env.CONTAINER_NAME}" `
                        -p 8081:8080 `
                        "${env.IMAGE_NAME}:${env.IMAGE_TAG}"

                    if (\$LASTEXITCODE -ne 0) {
                        throw 'Container deployment failed.'
                    }
                """
            }
        }

        stage('Verify Deployment') {
            steps {
                powershell """
                    \$ErrorActionPreference = 'Stop'
                    \$ok = \$false

                    for (\$i = 0; \$i -lt 30; \$i++) {
                        try {
                            \$response = Invoke-WebRequest `
                                -Uri 'http://localhost:8081/digital-wallet/' `
                                -TimeoutSec 3 `
                                -UseBasicParsing

                            if (\$response.StatusCode -eq 200) {
                                \$ok = \$true
                                break
                            }
                        } catch {
                            Start-Sleep -Seconds 2
                        }
                    }

                    if (-not \$ok) {
                        docker logs "${env.CONTAINER_NAME}"
                        throw 'Deployment verification failed.'
                    }

                    docker ps --filter "name=${env.CONTAINER_NAME}"
                    Write-Host 'Deployment passed: HTTP 200'
                """
            }
        }
    }

    post {
        success {
            echo 'Digital Wallet image published and container deployed successfully.'
        }

        failure {
            echo 'Pipeline failed. Review the failed stage logs.'
        }
    }
}
