def call() {
  stage('Cleanup') {
    sh "rm -rf /tmp/docker/${env.BUILD_TAG}"
  }
}
