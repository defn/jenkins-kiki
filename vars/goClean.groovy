def call() {
  stage('Clean') {
    figlet("clean")
    sh("rm -rf /tmp/docker/${env.BUILD_TAG}")
  }
}
