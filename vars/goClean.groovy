def call() {
  stage('Clean') {
    sh("/env.sh figlet -f /j/chunky.flf clean")
    sh "rm -rf /tmp/docker/${env.BUILD_TAG}"
  }
}
