pipeline {

    agent any

    // A configurer dans "Administrer Jenkins > Configuration globale des outils"
    // (mêmes étapes que la Partie 4 du TP0 : Ajouter JDK / Ajouter Maven)
    tools {
        jdk 'JDK17'
        maven 'Maven3'
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
                git branch: 'main', url: 'https://github.com/VOTRE_COMPTE/GestionStockPharmacie.git'
            }
        }

        stage('Build') {
            steps {
                echo 'Compilation du projet Maven'
                sh 'mvn -B clean compile'
            }
        }

        stage('Tests unitaires') {
            steps {
                echo 'Exécution des tests JUnit/Mockito'
                sh 'mvn -B test'
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
                    sh "mvn -B sonar:sonar -Dsonar.projectKey=${SONAR_PROJECT_KEY}"
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
                sh 'mvn -B package -DskipTests'
                archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
            }
        }

        stage('Déploiement Nexus') {
            when {
                expression { params.ENVIRONNEMENT != 'dev' }
            }
            steps {
                echo "Publication de l'artefact sur Nexus (${params.ENVIRONNEMENT})"
                sh 'mvn -B deploy -DskipTests'
            }
        }
    }

    post {
        success {
            echo 'Pipeline terminé avec succès.'
            mail to: 'equipe-dev@pharmacie.local',
                 subject: "SUCCESS: ${env.JOB_NAME} #${env.BUILD_NUMBER}",
                 body: "Le build ${env.BUILD_NUMBER} du job ${env.JOB_NAME} a réussi.\nVoir : ${env.BUILD_URL}"
        }
        unstable {
            mail to: 'equipe-dev@pharmacie.local',
                 subject: "UNSTABLE: ${env.JOB_NAME} #${env.BUILD_NUMBER}",
                 body: "Le build est instable (tests échoués ou Quality Gate en alerte).\nVoir : ${env.BUILD_URL}"
        }
        failure {
            mail to: 'equipe-dev@pharmacie.local',
                 subject: "FAILED: ${env.JOB_NAME} #${env.BUILD_NUMBER}",
                 body: "Le build ${env.BUILD_NUMBER} a échoué.\nVoir : ${env.BUILD_URL}"
        }
    }
}
