export type ReservationProposalStatus = 'PENDING' | 'APPROVED' | 'REJECTED'

export type ReservationProposal = {
  id: string
  status: ReservationProposalStatus
  customerName: string
  branch: string
  purpose: string
  dateTime: string
}
