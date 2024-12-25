export interface PaymentSessionResponse {
  status: PaymentStatus // "SUCCESS"
  message: string // "Payment session created successfully"
  httpStatus: number // 200
  data: {
    sessionId: string // "cs_test_a1hQrNmq85xCNUtPEb2aJemoj6xL4I1jmvXma6rcnu7iT8bjbM5M8bGfjq"
    sessionUrl: string // URL for the payment session
  }
}

export enum PaymentStatus {
  SUCCESS = 'SUCCESS',
  FAILURE = 'FAILURE',
}
