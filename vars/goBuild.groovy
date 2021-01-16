def call() {
  stage('Build') {
    sh """
      /env.sh figlet -f /j/chunky.flf build
      /env.sh /app/src/bin/goreleaser build --rm-dist
    """
  }
}
