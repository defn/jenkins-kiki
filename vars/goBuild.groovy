def call() {
  stage('Build') {
    sh "/env.sh goreleaser build --rm-dist"
  }
}
