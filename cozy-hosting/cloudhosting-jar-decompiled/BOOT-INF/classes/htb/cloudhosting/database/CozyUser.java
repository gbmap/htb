package htb.cloudhosting.database;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(
   name = "users",
   schema = "public"
)
public class CozyUser {
   @Id
   @Column
   private String name;
   @Column
   private String password;
   @Column
   private String role;

   public String getName() {
      return this.name;
   }

   public String getPassword() {
      return this.password;
   }

   public String getRole() {
      return this.role;
   }

   public void setName(final String name) {
      this.name = name;
   }

   public void setPassword(final String password) {
      this.password = password;
   }

   public void setRole(final String role) {
      this.role = role;
   }

   @Override
   public boolean equals(final Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof CozyUser)) {
         return false;
      } else {
         CozyUser other = (CozyUser)o;
         if (!other.canEqual(this)) {
            return false;
         } else {
            Object this$name = this.getName();
            Object other$name = other.getName();
            if (this$name == null ? other$name == null : this$name.equals(other$name)) {
               Object this$password = this.getPassword();
               Object other$password = other.getPassword();
               if (this$password == null ? other$password == null : this$password.equals(other$password)) {
                  Object this$role = this.getRole();
                  Object other$role = other.getRole();
                  return this$role == null ? other$role == null : this$role.equals(other$role);
               } else {
                  return false;
               }
            } else {
               return false;
            }
         }
      }
   }

   protected boolean canEqual(final Object other) {
      return other instanceof CozyUser;
   }

   @Override
   public int hashCode() {
      int PRIME = 59;
      int result = 1;
      Object $name = this.getName();
      result = result * 59 + ($name == null ? 43 : $name.hashCode());
      Object $password = this.getPassword();
      result = result * 59 + ($password == null ? 43 : $password.hashCode());
      Object $role = this.getRole();
      return result * 59 + ($role == null ? 43 : $role.hashCode());
   }

   @Override
   public String toString() {
      return "CozyUser(name=" + this.getName() + ", password=" + this.getPassword() + ", role=" + this.getRole() + ")";
   }
}
