def call(Map p = null, Closure body = null) {
  node() {
    def success = false

    try {
      goPrep()

      def param = p

      if (param == null) {
        param = readYaml file: 'jenkins.yaml'
      }

      withCredentials([[
        $class: 'VaultTokenCredentialBinding',
        credentialsId: 'VaultToken',
        vaultAddr: env.VAULT_ADDR ]]) {

        sh("/env.sh figlet -f /j/chunky.flf secrets")

        def role = param.name.replaceAll("/", "--")

        def WRAPPED_SID = ''
        env.WRAPPED_SID = sh(returnStdout: true, script: "/env.sh vault write -field=wrapping_token -wrap-ttl=60s -f auth/approle/role/${role}/secret-id").trim()
      
        def UNWRAPPED_SID = ''
        env.UNWRAPPED_SID= sh(returnStdout: true, script: 'set +x; /env.sh vault unwrap -field=secret_id ${WRAPPED_SID}; set -x').trim()

        def pipelineConfiguration = creds(param.roleId, env.UNWRAPPED_SID)

        def jenkinsSecrets = [
          path: 'kv/jenkins/common',
          secretValues: [
            [vaultKey: 'GITHUB_TOKEN'],
            [vaultKey: 'DOCKER_USERNAME'],
            [vaultKey: 'DOCKER_PASSWORD']
          ]
        ]

        def pipelineSecrets = [[
          path: 'kv/pipeline/' + role,
          secretValues: (param.secretValues != null ? param.secretValues : [])
        ]]

        withVault([vaultSecrets: pipelineSecrets + jenkinsSecrets, configuration: pipelineConfiguration]) {
          withEnv([
            "DOCKER_CONFIG=/tmp/docker/${env.BUILD_TAG}",
            "VAULT_ADDR=", "VAULT_TOKEN=", "GITHUB_TOKEN=", "DOCKER_USERNAME=",
            "DOCKER_PASSWORD=", "UNWRAPPED_SID=", "WRAPPED_SID="]) {
            if (body != null) {
              body()
            }
            else {
              if (fileExists(".goreleaser.yml")) {
                goreleaserMain()
              }
            }
          }
        }
      }

      goClean()

      success = true
    }
    finally {
      ansiColor('xterm-256color') {
        if (success) {
          sh("set +x; /env.sh figlet -f /j/broadway.flf pass | lolcat -f; echo")
        }
        else {
          sh("set +x; /env.sh figlet -f /j/broadway.flf fail | lolcat -f; echo")
        }
      }
    }
  }
}
