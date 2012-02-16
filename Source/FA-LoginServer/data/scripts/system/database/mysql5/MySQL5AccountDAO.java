package mysql5;

import org.apache.log4j.Logger;
import commons.database.DB;
import commons.database.IUStH;
import loginserver.dao.AccountDAO;
import loginserver.model.Account;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * MySQL5 Account DAO implementation
 *
 * @author SoulKeeper
 */
public class MySQL5AccountDAO extends AccountDAO
{
	/**
	 * Logger
	 */
	private static final Logger	log = Logger.getLogger(MySQL5AccountDAO.class);

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Account getAccount(String name)
	{
		Account				account = null;
		PreparedStatement	st      = DB.prepareStatement("SELECT * FROM account_data WHERE `name` = ?");

		try
		{
			st.setString(1, name);

			ResultSet	rs = st.executeQuery();

			if(rs.next())
			{
				account = new Account();

				account.setId(rs.getInt("id"));
				account.setName(name);
				account.setPasswordHash(rs.getString("password"));
				account.setAccessLevel(rs.getByte("access_level"));
				account.setMembership(rs.getByte("membership"));
				account.setActivated(rs.getByte("activated"));
				account.setLastServer(rs.getByte("last_server"));
				account.setLastIp(rs.getString("last_ip"));
				account.setIpForce(rs.getString("ip_force"));
                account.setExpire(rs.getDate("expire"));
			}
		}
		catch (Exception e)
		{
			log.error("Can't select account with name: " + name, e);
		}
		finally
		{
			DB.close(st);
		}

		return account;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getAccountId(String name)
	{
		int					id = -1;
		PreparedStatement	st = DB.prepareStatement("SELECT `id` FROM account_data WHERE `name` = ?");

		try
		{
			st.setString(1, name);

			ResultSet	rs = st.executeQuery();

			rs.next();

			id = rs.getInt("id");
		}
		catch (SQLException e)
		{
			log.error("Can't select id after account insertion", e);
		}
		finally
		{
			DB.close(st);
		}

		return id;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getAccountCount()
	{
		PreparedStatement	st = DB.prepareStatement("SELECT count(*) AS c FROM account_data");
		ResultSet			rs = DB.executeQuerry(st);

		try
		{
			rs.next();

			return rs.getInt("c");
		}
		catch (SQLException e)
		{
			log.error("Can't get account count", e);
		}
		finally
		{
			DB.close(st);
		}

		return -1;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean insertAccount(Account account)
	{
		int					result = 0;
		PreparedStatement	st     =
			DB.prepareStatement("INSERT INTO account_data(`name`, `password`, access_level, membership, activated, last_server, last_ip, ip_force, expire) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)");

		try
		{
			st.setString(1, account.getName());
			st.setString(2, account.getPasswordHash());
			st.setByte(3, account.getAccessLevel());
			st.setByte(4, account.getMembership());
			st.setByte(5, account.getActivated());
			st.setByte(6, account.getLastServer());
			st.setString(7, account.getLastIp());
			st.setString(8, account.getIpForce());
			st.setDate(9, account.getExpire());

			result = st.executeUpdate();
		}
		catch (SQLException e)
		{
			log.error("Can't inser account", e);
		}
		finally
		{
			DB.close(st);
		}

		if(result > 0)
		{
			account.setId(getAccountId(account.getName()));
		}

		return result > 0;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean updateAccount(Account account)
	{
		int					result = 0;
		PreparedStatement	st     =
			DB.prepareStatement("UPDATE account_data SET `name` = ?, `password` = ?, access_level = ?, membership = ?, last_server = ?, last_ip = ?, ip_force = ?, expire = ? WHERE `id` = ?");

		try
		{
			st.setString(1, account.getName());
			st.setString(2, account.getPasswordHash());
			st.setByte(3, account.getAccessLevel());
			st.setByte(4, account.getMembership());
			st.setByte(5, account.getLastServer());
			st.setString(6, account.getLastIp());
			st.setString(7, account.getIpForce());
			st.setDate(8, account.getExpire());
			st.setInt(9, account.getId());

			st.executeUpdate();
		}
		catch (SQLException e)
		{
			log.error("Can't update account");
		}
		finally
		{
			DB.close(st);
		}

		return result > 0;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean updateLastServer(final int accountId, final byte lastServer)
	{
		return DB.insertUpdate("UPDATE account_data SET last_server = ? WHERE id = ?", new IUStH()
		{
			@Override
			public void handleInsertUpdate(PreparedStatement preparedStatement) throws SQLException
			{
				preparedStatement.setByte(1, lastServer);
				preparedStatement.setInt(2, accountId);
				preparedStatement.execute();
			}
		});
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean updateLastIp(final int accountId, final String ip)
	{
		return DB.insertUpdate("UPDATE account_data SET last_ip = ? WHERE id = ?", new IUStH()
		{
			@Override
			public void handleInsertUpdate(PreparedStatement preparedStatement) throws SQLException
			{
				preparedStatement.setString(1, ip);
				preparedStatement.setInt(2, accountId);
				preparedStatement.execute();
			}
		});
	}
	
    public boolean updateMembership(final int accountId) {
        return DB.insertUpdate("UPDATE account_data SET membership = 0, expire = NULL WHERE id = ? and expire < CURRENT_TIMESTAMP", new IUStH() {
            @Override
            public void handleInsertUpdate(PreparedStatement preparedStatement) throws SQLException {
                preparedStatement.setInt(1, accountId);
                preparedStatement.execute();
            }
        });
    }

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getLastIp(final int accountId)
	{
		String				lastIp	= "";
		PreparedStatement	st		= DB.prepareStatement("SELECT `last_ip` FROM `account_data` WHERE `id` = ?");

		try
		{
			st.setInt(1, accountId);
			ResultSet rs = st.executeQuery();
			if(rs.next())
			{
				lastIp = rs.getString("last_ip");
			}
		}
		catch (Exception e)
		{
			log.error("Can't select last IP of account ID: " + accountId, e);
			return "";
		}
		finally
		{
			DB.close(st);
		}

		return lastIp;
	}
	
	/**
     * {@inheritDoc}
     */
    @Override
	   public long getLastLogout(final int accountId)
	    {
	        long last_logout  = 0;
	        PreparedStatement   st      = DB.prepareStatement("SELECT `last_logout` FROM `account_data` WHERE `id` = ?");

	        try
	        {
	            st.setInt(1, accountId);
	            ResultSet rs = st.executeQuery();
	            if(rs.next())
	            {
	                last_logout = rs.getLong("last_logout");
	            }
	        }
	        catch (Exception e)
	        {
	            log.error("Can't select last logout time of account ID: " + accountId, e);
	            return 0;
	        }
	        finally
	        {
	            DB.close(st);
	        }

	        return last_logout;
	    }

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean supports(String database, int majorVersion, int minorVersion)
	{
		return MySQL5DAOUtils.supports(database, majorVersion, minorVersion);
	}
}
