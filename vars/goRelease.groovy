def call() {
  stage('Release') {
    sh """
      /env.sh figlet -f /j/chunky.flf release
      /env.sh /app/src/bin/goreleaser release --rm-dist
    """
  }
}
