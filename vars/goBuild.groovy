def call() {
  stage('Build') {
    figlet("build")
    sh("/env.sh /app/src/bin/goreleaser build --rm-dist")
  }
}
