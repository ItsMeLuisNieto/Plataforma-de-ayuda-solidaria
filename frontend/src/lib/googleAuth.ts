export interface GoogleCredentialResponse {
  credential: string
}

interface GoogleIdConfiguration {
  client_id: string
  callback: (response: GoogleCredentialResponse) => void
}

interface GoogleButtonConfiguration {
  theme: string
  size: string
  text: string
  shape: string
}

interface GoogleAccountsId {
  initialize(configuration: GoogleIdConfiguration): void
  renderButton(container: HTMLElement, configuration: GoogleButtonConfiguration): void
  prompt(): void
}

interface GoogleSdk {
  accounts: { id: GoogleAccountsId }
}

declare global {
  interface Window {
    google?: GoogleSdk
  }
}

export const GOOGLE_CLIENT_ID =
  '122831283261-9gc5ojcgeetnllp6dcj15b3vbiosshbt.apps.googleusercontent.com'

export function loadGoogleIdentityScript(onLoad: () => void): void {
  if (window.google?.accounts?.id) {
    onLoad()
    return
  }

  const existente = document.querySelector<HTMLScriptElement>('script[data-google-identity="true"]')
  if (existente) {
    existente.addEventListener('load', onLoad, { once: true })
    return
  }

  const script = document.createElement('script')
  script.src = 'https://accounts.google.com/gsi/client'
  script.async = true
  script.defer = true
  script.dataset.googleIdentity = 'true'
  script.addEventListener('load', onLoad, { once: true })
  document.body.appendChild(script)
}

export function renderGoogleButton(
  container: HTMLElement,
  texto: 'signin_with' | 'signup_with',
  callback: (response: GoogleCredentialResponse) => void,
): void {
  const id = window.google?.accounts?.id
  if (!id) {
    return
  }

  id.initialize({
    client_id: GOOGLE_CLIENT_ID,
    callback,
  })

  id.renderButton(container, {
    theme: 'outline',
    size: 'large',
    text: texto,
    shape: 'rectangular',
  })

  id.prompt()
}
