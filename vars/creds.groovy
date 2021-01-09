import hudson.util.Secret
import com.cloudbees.plugins.credentials.CredentialsScope
import com.datapipe.jenkins.vault.credentials.VaultAppRoleCredential

def call(secretId) {
    VaultAppRoleCredential pipelineCredential = new VaultAppRoleCredential(
    CredentialsScope.GLOBAL,
    NM_JOB + '-vault', NM_JOB + '-vault',
    pipelineRoleId,
    Secret.fromString(secretId),
    "approle"
  )

  def pipelineConfiguration = [
    vaultCredential: pipelineCredential
  ]

  return pipelineConfiguration
}
