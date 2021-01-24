def call() {
  figlet("prep")

  checkout scm

  if (env.TAG_NAME == null) {
    env.GORELEASER_CURRENT_TAG = "0.${env.CHANGE_ID ?: 0}.${env.BUILD_ID}-${env.BUILD_TAG}"

    stage('Tag') {
      sh "git tag ${env.GORELEASER_CURRENT_TAG}"
    }
  }
  else {
    env.GORELEASER_CURRENT_TAG = env.TAG_NAME
  }
}
