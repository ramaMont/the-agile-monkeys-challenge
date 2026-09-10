export type ReservationProposalStatus = 'PENDING' | 'APPROVED' | 'REJECTED'

export type ReservationProposal = {
  id: string
  status: ReservationProposalStatus
  customerName: string
  branch: string
  purpose: string
  dateTime: string
}

export type ReservationProposalPage = {
  content: ReservationProposal[]
  page: number
  size: number
  totalElements: number
  totalPages: number
}
