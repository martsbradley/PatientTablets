package martinbradley.hospital.persistence.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityTransaction;
import javax.transaction.UserTransaction;


class UserTransactionAdapter implements UserTransaction
{
    final EntityTransaction entityTransaction;
    private static final Logger log = LoggerFactory.getLogger(UserTransactionAdapter.class);

    public UserTransactionAdapter(EntityTransaction entityTransaction)
    {
        this.entityTransaction = entityTransaction;
    }

    public void begin()
    {
        log.info("begin has been CALLED...");
        entityTransaction.begin();
    }

    public void commit()
    {
        log.info("commit has been CALLED...");
        entityTransaction.commit();
    }

    public int  getStatus(){ return 0;}

    public void rollback()
    {
        notSupported();
    }

    public void setRollbackOnly()
    {
        notSupported();
    }

    public void setTransactionTimeout(int seconds)
    {
        notSupported();
    }

    private void notSupported()
    {
        throw new RuntimeException();
    }
}
