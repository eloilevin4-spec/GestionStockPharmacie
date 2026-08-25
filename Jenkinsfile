pipeline {

    agent any

    tools {
        jdk 'JDK-21'
        maven 'Maven-3.9'
    }

    options {
        timestamps()
        skipStagesAfterUnstable()
    }

    parameters {
        choice(name: 'ENVIRONNEMENT', choices: ['dev', 'staging', 'prod'], description: 'Environnement cible du déploiement')
    }

    environment {
        SONAR_PROJECT_KEY = 'GestionStockPharmacie'
    }

    stages {

        stage('Checkout') {
            steps {
                echo "Récupération du code source depuis Git (branche: ${env.BRANCH_NAME ?: 'main'})"
                git branch: 'main', url: 'https://github.com/eloilevin4-spec/GestionStockPharmacie.git'
            }
        }

        stage('Build') {
            steps {
                echo 'Compilation du projet Maven'
                bat 'mvn -B clean compile'
            }
        }

        stage('Tests unitaires') {
            steps {
                echo 'Exécution des tests JUnit/Mockito'
                bat 'mvn -B test'
            }
            post {
                always {
                    junit '**/target/surefire-reports/*.xml'
                }
            }
        }

        stage('Analyse qualité - SonarQube') {
            steps {
                withSonarQubeEnv('SonarServer') {
                    bat "mvn -B sonar:sonar -Dsonar.projectKey=%SONAR_PROJECT_KEY%"
                }
            }
        }

        stage('Quality Gate') {
            steps {
                timeout(time: 5, unit: 'MINUTES') {
                    waitForQualityGate abortPipeline: true
                }
            }
        }

        stage('Package') {
            steps {
                echo 'Génération du .jar'
                bat 'mvn -B package -DskipTests'
                archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
            }
        }

        stage('Déploiement Nexus') {
            when {
                expression { params.ENVIRONNEMENT != 'dev' }
            }
            steps {
                echo "Publication de l'artefact sur Nexus (${params.ENVIRONNEMENT})"
                bat 'mvn -B deploy -DskipTests'
            }
        }
    }

    post {
        success {
            echo 'Pipeline terminé avec succès.'
            script {
                try {
                    mail to: 'equipe-dev@pharmacie.local',
                         subject: "SUCCESS: ${env.JOB_NAME} #${env.BUILD_NUMBER}",
                         body: "Le build ${env.BUILD_NUMBER} du job ${env.JOB_NAME} a réussi.\nVoir : ${env.BUILD_URL}"
                } catch (e) {
                    echo "Notification email non envoyée (SMTP non configuré) : ${e.message}"
                }
            }
        }
        unstable {
            script {
                try {
                    mail to: 'equipe-dev@pharmacie.local',
                         subject: "UNSTABLE: ${env.JOB_NAME} #${env.BUILD_NUMBER}",
                         body: "Le build est instable (tests échoués ou Quality Gate en alerte).\nVoir : ${env.BUILD_URL}"
                } catch (e) {
                    echo "Notification email non envoyée (SMTP non configuré) : ${e.message}"
                }
            }
        }
        failure {
            script {
                try {
                    mail to: 'equipe-dev@pharmacie.local',
                         subject: "FAILED: ${env.JOB_NAME} #${env.BUILD_NUMBER}",
                         body: "Le build ${env.BUILD_NUMBER} a échoué.\nVoir : ${env.BUILD_URL}"
                } catch (e) {
                    echo "Notification email non envoyée (SMTP non configuré) : ${e.message}"
                }
            }
        }
    }
}