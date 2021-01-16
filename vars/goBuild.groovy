def call() {
  stage('Build') {
    sh """
      /env.sh figlet -f /j/chunky.flf build
      /env.sh goreleaser build --rm-dist
    """
  }
}
