pipeline {
    agent {
        kubernetes {
            inheritFrom 'esign3'
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
    	stage('Hello') {
          steps {
		        echo 'Hello World'
		        echo 'Building...'
		        
		        echo 'Env vars for cloud pull request...'
		        echo "BITBUCKET_SOURCE_BRANCH ${env.BITBUCKET_SOURCE_BRANCH}"
		        echo "BITBUCKET_TARGET_BRANCH ${env.BITBUCKET_TARGET_BRANCH}"
		        echo "BITBUCKET_PULL_REQUEST_LINK ${env.BITBUCKET_PULL_REQUEST_LINK}"
		        echo "BITBUCKET_PULL_REQUEST_ID ${env.BITBUCKET_PULL_REQUEST_ID}"
		        echo "BITBUCKET_PAYLOAD ${env.BITBUCKET_PAYLOAD}"
		
		        echo 'Env vars for cloud push...'
		        echo "REPOSITORY_LINK ${env.REPOSITORY_LINK}"
		        echo "BITBUCKET_SOURCE_BRANCH ${env.BITBUCKET_SOURCE_BRANCH}"
		        echo "BITBUCKET_REPOSITORY_URL ${env.BITBUCKET_REPOSITORY_URL}"
		        echo "BITBUCKET_PUSH_REPOSITORY_UUID ${env.BITBUCKET_PUSH_REPOSITORY_UUID}"
		        echo "BITBUCKET_PAYLOAD ${env.BITBUCKET_PAYLOAD}"
		
		        echo 'Env vars for server push...'
		        echo "REPOSITORY_LINK ${env.REPOSITORY_LINK}"
		        echo "BITBUCKET_SOURCE_BRANCH ${env.BITBUCKET_SOURCE_BRANCH}"
		        echo "BITBUCKET_REPOSITORY_URL ${env.BITBUCKET_REPOSITORY_URL}"
		        echo "BITBUCKET_PUSH_REPOSITORY_UUID ${env.BITBUCKET_PUSH_REPOSITORY_UUID}"
		        echo "BITBUCKET_PAYLOAD ${env.BITBUCKET_PAYLOAD}"
            }
        }
        stage('Cleanup'){
            steps{
            	echo "cleanup false"
                // Clean before build
                //cleanWs()
            }
        }
        stage('checkout') {
             environment { 
                //My_Git_token = credentials('nik_cred_bitb_prod_esign')
                gitBranch = 'develop';
                refsb = "${env.refsb != null ? env.refsb : 'develop'}";
                hrefr = "${env.hrefr_0 != null ? env.hrefr_0 : env.BITB_REPO + '/sidecar-logging.git'}";
                //hrefr = "${env.hrefr}";
                eventKey = "${env.eventKey}";
                TYPE = "${env.type}";
                DEPLOY_K8S = false;
            }
            steps {
                script{
                
                withCredentials([gitUsernamePassword(credentialsId: 'nik_cred_bitb_prod_esign', gitToolName: 'git-tool')]) {
                   
                    echo 'checkout sidecar-logging'
                    if(TYPE == 'TAG'){
                        DEPLOY_K8S = true;
                        sh "git clone -b $gitBranch $hrefr"
                        dir('docker-test'){
                            sh "git checkout tags/$refsb -b $refsb"
                            COMMIT_ID = sh(returnStdout: true, script: 'git rev-list --tags --date-order | head -1').trim()
                        	PPR_TAG = sh(returnStdout: true, script: "git show-ref --tags | grep '${COMMIT_ID}' | tail -1 | awk -F /  \'{print  \$NF}\'").trim()
                        }
                        
                    }else{
                    	sh "pwd"
                    	sh "ls -l"
                        sh "mvn -v"
                        sh "git clone -b SC-100 $hrefr"
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
                        sh "pwd"
                        sh "ls -l"
		                container('kubectl'){
		                   sh "cat sidecar-logging.yml | sed -e 's/\${DOCKER_REPO}/'$DOCKER_REPO'/g;s/\${DOCKER_TAG}/'$DOCKER_TAG'/g' | kubectl -n esoff apply -f -"
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
            echo "URL: ${env.JENKINS_URL}job/${env.JOB_NAME}/${env.BUILD_ID}/console"
            //mail bcc: '', body: "job ${env.JOB_NAME} sucess. PR-Branch: ${env.refsb} click here for job details: ${env.JENKINS_URL}job/${env.JOB_NAME}/${env.BUILD_ID}/console", cc: 'kablum@proteantech.in', from: 'no-reply@devops.proteantech.in', replyTo: '', subject: "PR:${env.refsb} ${env.JOB_NAME}-Success", to: 'nikhilw@proteantech.in'
            //currentBuild.result = currentBuild.result ?: 'SUCCESS'
            emailext body: '${JELLY_SCRIPT, template="jenkins-matrix-email-html.template"}', 
            subject: "${env.JOB_NAME} - Build # ${env.BUILD_ID} - ${currentBuild.currentResult}!", 
            to: 'nikhilw@proteantech.in;kablum@proteantech.in'
            //bitbucketStatusNotify(buildState:'SUCCESSFUL', commitId: "${COMMIT_ID}")
        }
        
    }
}

