/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package entities;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import repositories.RepositoryObject;

/**
 *
 * @author Szymon Skrzyński <skrzynski.szymon@gmail.com>
 */
@Entity
@Table(name = "file")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "File.findAll", query = "SELECT f FROM File f"),
    @NamedQuery(name = "File.findById", query = "SELECT f FROM File f WHERE f.id = :id"),
    @NamedQuery(name = "File.findByName", query = "SELECT f FROM File f WHERE f.name = :name"),
    @NamedQuery(name = "File.findByMimeType", query = "SELECT f FROM File f WHERE f.mimeType = :mimeType"),
    @NamedQuery(name = "File.findByCreatedAt", query = "SELECT f FROM File f WHERE f.createdAt = :createdAt"),
    @NamedQuery(name = "File.findByUpload", query = "SELECT f FROM File f WHERE f.upload = :upload")})
public class File extends RepositoryObject implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;
    @Basic(optional = false)
    @Column(name = "name")
    private String name;
    @Column(name = "mime_type")
    private String mimeType;
    @Basic(optional = false)
    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;
    @Column(name = "upload")
    private Short upload;
    @OneToMany(mappedBy = "parentId")
    private Collection<File> fileCollection;
    @JoinColumn(name = "parent_id", referencedColumnName = "id")
    @ManyToOne
    private File parentId;
    @JoinColumn(name = "owner_user_id", referencedColumnName = "id")
    @ManyToOne
    private User ownerUserId;

    public File() {
    }

    public File(Long id) {
        this.id = id;
    }

    public File(Long id, String name, Date createdAt) {
        this.id = id;
        this.name = name;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Short getUpload() {
        return upload;
    }

    public void setUpload(Short upload) {
        this.upload = upload;
    }

    @XmlTransient
    public Collection<File> getFileCollection() {
        return fileCollection;
    }

    public void setFileCollection(Collection<File> fileCollection) {
        this.fileCollection = fileCollection;
    }

    public File getParentId() {
        return parentId;
    }

    public void setParentId(File parentId) {
        this.parentId = parentId;
    }

    public User getOwnerUserId() {
        return ownerUserId;
    }

    public void setOwnerUserId(User ownerUserId) {
        this.ownerUserId = ownerUserId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof File)) {
            return false;
        }
        File other = (File) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entities.File[ id=" + id + " ]";
    }
    
}
