
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
                    def candidate =
                        "digital-wallet-candidate-${env.BUILD_NUMBER}"

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
                        credentialsId: 'dockerhub-pat-2026',
                        usernameVariable: 'DOCKERHUB_USER',
                        passwordVariable: 'DOCKERHUB_TOKEN'
                    )
                ]) {
                    powershell '''
                        $ErrorActionPreference = 'Stop'

                        $user = $env:DOCKERHUB_USER
                        $token = $env:DOCKERHUB_TOKEN

                        if ([string]::IsNullOrWhiteSpace($user)) {
                            throw 'Docker Hub username is missing.'
                        }

                        if ([string]::IsNullOrWhiteSpace($token)) {
                            throw 'Docker Hub token is missing.'
                        }

                        $user = $user.Trim()

                        $versionedImage = "$($env:IMAGE_NAME):$($env:IMAGE_TAG)"
                        $latestImage = "$($env:IMAGE_NAME):latest"

                        $dockerConfig = Join-Path $env:TEMP `
                            "jenkins-docker-config-$env:BUILD_NUMBER"

                        $previousDockerConfig = $env:DOCKER_CONFIG
                        $loginSucceeded = $false

                        New-Item -ItemType Directory `
                            -Path $dockerConfig -Force | Out-Null

                        $env:DOCKER_CONFIG = $dockerConfig

                        try {
                            Write-Host 'Checking Docker CLI...'
                            docker --version

                            if ($LASTEXITCODE -ne 0) {
                                throw 'Docker CLI is unavailable.'
                            }

                            Write-Host "Checking local image: $versionedImage"
                            docker image inspect $versionedImage

                            if ($LASTEXITCODE -ne 0) {
                                throw "Local image not found: $versionedImage"
                            }

                            Write-Host 'Attempting Docker Hub login...'

                            # Pass the token through standard input.
                            # Never print the token or include it in an error.
                            $loginOutput = @(
                                $token | docker login `
                                    --username $user `
                                    --password-stdin 2>&1
                            )

                            $loginExitCode = $LASTEXITCODE

                            foreach ($line in $loginOutput) {
                                Write-Host "$line"
                            }

                            if ($loginExitCode -ne 0) {
                                throw "Docker Hub login failed with exit code $loginExitCode. Review the Docker response above and verify the Jenkins credential ID, username, and token permissions."
                            }

                            $loginSucceeded = $true
                            Write-Host 'Docker Hub login successful.'

                            Write-Host "Pushing $versionedImage..."
                            docker push $versionedImage

                            if ($LASTEXITCODE -ne 0) {
                                throw 'Versioned image push failed.'
                            }

                            Write-Host "Pushing $latestImage..."
                            docker push $latestImage

                            if ($LASTEXITCODE -ne 0) {
                                throw 'Latest image push failed.'
                            }

                            Write-Host 'SUCCESS: Both image tags were published.'
                            Write-Host "Repository: $($env:IMAGE_NAME)"
                        }
                        finally {
                            if ($loginSucceeded) {
                                docker logout 2>$null | Out-Null
                            }

                            if (Test-Path $dockerConfig) {
                                Remove-Item -Path $dockerConfig `
                                    -Recurse -Force `
                                    -ErrorAction SilentlyContinue
                            }

                            if ($null -eq $previousDockerConfig) {
                                Remove-Item Env:DOCKER_CONFIG `
                                    -ErrorAction SilentlyContinue
                            } else {
                                $env:DOCKER_CONFIG = $previousDockerConfig
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

                    Write-Host 'Removing previous application container.'
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
