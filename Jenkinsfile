pipeline {
    agent {
        kubernetes {
            inheritFrom 'ecc'
            defaultContainer 'maven17'
        }
    }
    environment {
    	DEPLOY_K8S = false;
    	toHash = "${env.toHash}";
        built = false;
        BUILD_SCOPE = 'UAT'; 
    }
    stages {
        stage('checkout') {
             environment { 
                gitBranch = 'develop';
                refsb = "${env.refsb != null ? env.refsb : 'develop'}";
                hrefr = "${env.hrefr_0 != null ? env.hrefr_0 : env.SVN_URL + '/sidecar-logging.git'}";
                //hrefr = "${env.hrefr}";
                eventKey = "${env.eventKey}";
                TYPE = "${env.type}";
                DEPLOY_K8S = false;
            }
            steps {
                script{
                withCredentials([gitUsernamePassword(credentialsId: 'ecc-git-pat-cred', gitToolName: 'git-tool')]) {
                    echo 'checkout sidecar-logging'
                    if(TYPE == 'TAG'){
                        DEPLOY_K8S = true;
                        sh "git clone -b $gitBranch $hrefr"
                        dir('sidecar-logging'){
                            sh "git checkout tags/$refsb -b $refsb"
                            COMMIT_ID = sh(returnStdout: true, script: 'git rev-list --tags --date-order | head -1').trim()
                        	PPR_TAG = sh(returnStdout: true, script: "git show-ref --tags | grep '${COMMIT_ID}' | tail -1 | awk -F /  \'{print  \$NF}\'").trim()
                        }
                    }else{
                    	sh "pwd"
                    	sh "ls -l"
                        sh "mvn -v"
                        sh "git clone -b SL-100 $hrefr"
                        DEPLOY_K8S = true;
                        PPR_TAG = "${refsb}-beta"
                        COMMIT_ID = "${toHash}"
                    }
                    
                }
                }
            }
        }
        stage('Build sidecar-logging') {
            steps {
                script{
                    dir('sidecar-logging'){
                        sh "mvn clean install -DskipTests"
                    }
                }
            }
        }
        
        stage('Build Image') {
            steps {
                script{
                    dir('sidecar-logging'){
                        COMMIT_ID = sh(returnStdout: true, 
                        script: 'git rev-list --tags --date-order | head -1').trim()
                        echo "commitId:${COMMIT_ID}"
                        
                        DOCKER_TAG = sh(returnStdout: true,
                        script: "git show-ref --tags | grep '${COMMIT_ID}' | awk -F /  \'{print  \$NF}\'").trim()
                        DOCKER_TAG = "v${env.BUILD_NUMBER}"
                        echo "DOCKER_TAG:${DOCKER_TAG}"
                        if(DEPLOY_K8S){
                            echo "buiding docker images..."
                            container('docker'){
		                            sh "docker build . -t ${DOCKER_REPO}/sidecar-logging:${DOCKER_TAG}"
		                            sh "docker push ${DOCKER_REPO}/sidecar-logging:${DOCKER_TAG}"
		                        }
                        }else{
                            echo "skipping docker image build and push..."
                        }
                    }
                }
            }
        }
        stage('DeployK8s'){
            steps{
             script{
                dir('sidecar-logging'){
                    if(DEPLOY_K8S){
                    	echo "deploying to K8S..."
                        sh "pwd"
		                container('kubectl'){
		                   sh "cat sidecar-logging.yml | sed -e 's/\${DOCKER_REPO}/'$DOCKER_REPO'/g;s/\${DOCKER_TAG}/'$DOCKER_TAG'/g' | kubectl -n ecc apply -f -"
	                	}
                    }else{
                        echo "skipping K8S deploy..."
                    }
                }
             }
            }
        }
    }
    post {
        success {
            echo 'This will run only if successful'
        }
        
    }
}

