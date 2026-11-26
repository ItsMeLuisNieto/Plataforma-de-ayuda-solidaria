export interface UsuarioActual {
  id: number
  nombre: string
  email: string
  rol: string
}

export interface Campana {
  id: number
  nombre?: string | null
  titulo?: string | null
  descripcion: string
  metaDias: number
  porcentaje: number
  ubicacion: string
  beneficiarios: number
  meta: number
}

export interface Testimonio {
  id: number
  nombre: string
  rol: string
  comentario: string
}

export interface DonacionResumen {
  id?: number
  codigoLote?: string | null
  nombreDonante?: string | null
  producto?: string | null
  cantidad?: number | null
  estado?: string | null
  fechaIngreso?: string | null
  campana?: Pick<Campana, 'id' | 'nombre' | 'titulo' | 'ubicacion'> | null
}

export interface RecepcionDonacion {
  id: number
  donacion?: DonacionResumen | null
  campana?: Pick<Campana, 'id' | 'nombre' | 'titulo'> | null
  campanaNombre?: string | null
  estado: string
  fechaSolicitud?: string | null
  motivoRechazo?: string | null
}

export interface Movimiento {
  idTransaccion: number
  fecha: string
  campanaTitulo: string
  distrito: string
  tipoDonacion: string
  valorMonto: string
  estado: string
}

export interface Kpis {
  totalMontoRecaudado: number | string
  totalBeneficiariosAtendidos: number
  totalCampanasActivas?: number
  totalBienesStock: number
}
