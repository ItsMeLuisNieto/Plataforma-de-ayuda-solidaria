import type { Campana } from '../types'

type ReferenciaCampana = Pick<Campana, 'nombre' | 'titulo'> | null | undefined

export function tituloCampana(campana: ReferenciaCampana): string {
  return campana?.nombre ?? campana?.titulo ?? 'N/A'
}

export function formatearEntero(valor: number): string {
  return valor.toLocaleString('es-PE')
}

export function formatearFecha(valor?: string | null): string {
  if (!valor) {
    return ''
  }
  return valor.replace('T', ' ').substring(0, 19)
}
