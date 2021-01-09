def call(nmRole, pipelineRoleId, jenkinsSecrets, pipelineSecrets, Closure body) {
  goPrep()

  withCredentials([[
    $class: 'VaultTokenCredentialBinding',
    credentialsId: 'VaultToken',
    vaultAddr: env.VAULT_ADDR ]]) {

    def WRAPPED_SID = ''
    env.WRAPPED_SID = sh(returnStdout: true, script: "/env.sh vault write -field=wrapping_token -wrap-ttl=60s -f auth/approle/role/${nmRole}/secret-id").trim()
  
    def UNWRAPPED_SID = ''
    env.UNWRAPPED_SID= sh(returnStdout: true, script: '/env.sh vault unwrap -field=secret_id ${WRAPPED_SID}').trim()

    def pipelineConfiguration = creds(pipelineRoleId, env.UNWRAPPED_SID)

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
