// VerificationDetails.ts

export interface VerificationDetails {
  id: number
  isEmailVerified: boolean
  isPhoneNumberVerified: boolean
}

// UserDTO.ts

export interface UserDTO {
  id: number
  username: string
  phoneNumber: string
  email: string
  verificationDetails: VerificationDetails
}
