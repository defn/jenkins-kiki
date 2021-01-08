def call(cfg) {
  stage("cool beans") {
    sh script: "uname -a", label: "pinto"
  }
}
