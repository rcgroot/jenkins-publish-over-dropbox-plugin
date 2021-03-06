/*
 * The MIT License
 *
 * Copyright (C) 2015 by René de Groot
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package org.jenkinsci.plugins.publishoverdropbox.impl;

import hudson.model.Describable;
import jenkins.model.Jenkins;
import jenkins.plugins.publish_over.BPTransfer;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.jenkinsci.plugins.publishoverdropbox.descriptor.DropboxTransferDescriptor;
import org.kohsuke.stapler.DataBoundConstructor;

public class DropboxTransfer extends BPTransfer implements Describable<DropboxTransfer> {


    private static final long serialVersionUID = 1L;
    private final boolean pruneRoot;
    private final int pruneRootDays;

    @DataBoundConstructor
    public DropboxTransfer(final String sourceFiles, final String excludes, final String remoteDirectory, final String removePrefix,
                           final boolean remoteDirectorySDF, final boolean flatten, final boolean cleanRemote, final boolean pruneRoot, final int pruneRootDays) {
        super(sourceFiles, excludes, remoteDirectory, removePrefix, remoteDirectorySDF, flatten, cleanRemote, false, false, null);
        this.pruneRoot = pruneRoot;
        this.pruneRootDays = pruneRootDays;
    }

    public int getPruneRootDays() {
        return pruneRootDays;
    }

    public boolean isPruneRoot() {
        return pruneRoot;
    }

    public static boolean canUseExcludes() {
        return false;
    }

    public DropboxTransferDescriptor getDescriptor() {
        return Jenkins.getActiveInstance().getDescriptorByType(DropboxTransferDescriptor.class);
    }

    protected ToStringBuilder addToToString(final ToStringBuilder builder) {
        return super.addToToString(builder);
    }

    public boolean equals(final Object that) {
        if (this == that) return true;
        if (that == null || getClass() != that.getClass()) return false;

        return addToEquals(new EqualsBuilder(), (DropboxTransfer) that).isEquals();
    }

    public int hashCode() {
        return addToHashCode(new HashCodeBuilder()).toHashCode();
    }

    public String toString() {
        return addToToString(new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)).toString();
    }


}
