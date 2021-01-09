def call(pipelineRoleId, jenkinsSecrets, pipelineSecrets, nmJob, Closure body) {
  goPrep()

  withCredentials([[
    $class: 'VaultTokenCredentialBinding',
    credentialsId: 'VaultToken',
    vaultAddr: env.VAULT_ADDR ]]) {

    def PIPELINE_SECRET_ID= ''
    env.PIPELINE_SECRET_ID = sh(returnStdout: true, script: "./ci/build ${nmJob}").trim()
    def pipelineConfiguration = creds(pipelineRoleId, env.PIPELINE_SECRET_ID)

    withVault([vaultSecrets: jenkinsSecrets]) {
      withVault([vaultSecrets: pipelineSecrets, configuration: pipelineConfiguration]) {
        withEnv(["DOCKER_CONFIG=/tmp/docker/${env.BUILD_TAG}"]) {
          if (env.TAG_NAME) {
            goRelease()
            body()
          }
          else {
            goBuild()
            body()
          }
        }
      }
    }
  }

  goClean()
}
