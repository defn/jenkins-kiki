def call() {
  docker.image("defn/jenkins-go").inside {
    stage('Test: Go') {
      sh("make ci-go-test")
    }

    if (env.TAG_NAME) {
      goRelease()
    }
    else {
      goBuild()
    }
  }
}
