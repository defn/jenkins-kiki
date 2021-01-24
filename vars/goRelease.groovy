def call() {
  stage('Release') {
    figlet("release")
    sh("/env.sh /app/src/bin/goreleaser release --rm-dist")
  }
}
