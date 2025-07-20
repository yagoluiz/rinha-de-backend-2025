CREATE TYPE processor_type AS ENUM ('default', 'fallback');

CREATE TABLE payments (
  id SERIAL PRIMARY KEY,
  correlation_id UUID UNIQUE NOT NULL,
  amount NUMERIC(10,2) NOT NULL,
  processor processor_type NOT NULL,
  requested_at TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE INDEX idx_payments_processor ON payments(processor);
CREATE INDEX idx_payments_requested_at ON payments(requested_at);
