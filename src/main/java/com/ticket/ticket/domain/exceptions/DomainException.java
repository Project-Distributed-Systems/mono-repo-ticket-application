package com.ticket.ticket.domain.exceptions;

import com.ticket.ticket.domain.event.TicketId;

// domain/exceptions/DomainException.java
public class DomainException extends RuntimeException {
  public DomainException(String message) {
    super(message);
  }

  public static class TicketAlreadyUsedException extends DomainException {
    public TicketAlreadyUsedException(TicketId ticketId) {
      super("Não é possível cancelar um ticket já utilizado. ticketId=" + ticketId.value());
    }
  }

  public static class AttemptToCancelANonExistentTicket extends DomainException {
    public AttemptToCancelANonExistentTicket(TicketId ticketId) {
      super("Não é possível cancelar um ticket que nunca foi criado. ticketId=" + ticketId.value());
    }
  }

  public static class AttemptToPayANonExistentTicket extends DomainException {
    public AttemptToPayANonExistentTicket(TicketId ticketId) {
      super("Não é possível pagar um ticket que nunca foi criado. ticketId=" + ticketId.value());
    }
  }

  public static class AttemptToPayATicketAlredyCanceled extends DomainException {
    public AttemptToPayATicketAlredyCanceled(TicketId ticketId) {
      super("Não é possível pagar um ticket que foi cancelado. ticketId=" + ticketId.value());
    }
  }
}
