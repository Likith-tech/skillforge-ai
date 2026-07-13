import { useEffect, useState } from 'react';
import Card from '../../components/ui/Card';
import Input from '../../components/ui/Input';
import Button from '../../components/ui/Button';
import Alert from '../../components/ui/Alert';
import Spinner from '../../components/ui/Spinner';
import { getMyCompany, createCompany, updateCompany } from '../../services/companyService';
import { getErrorMessage } from '../../utils/getErrorMessage';

const EMPTY_FORM = { companyName: '', description: '', website: '', location: '', logo: '' };

export default function CompanyProfile() {
  const [company, setCompany] = useState(null);
  const [form, setForm] = useState(EMPTY_FORM);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState(null);
  const [success, setSuccess] = useState(false);

  useEffect(() => {
    getMyCompany()
      .then((data) => {
        setCompany(data);
        setForm({
          companyName: data.companyName ?? '',
          description: data.description ?? '',
          website: data.website ?? '',
          location: data.location ?? '',
          logo: data.logo ?? '',
        });
      })
      .catch(() => setCompany(null))
      .finally(() => setLoading(false));
  }, []);

  function handleChange(event) {
    const { name, value } = event.target;
    setForm((prev) => ({ ...prev, [name]: value }));
    setSuccess(false);
  }

  async function handleSubmit(event) {
    event.preventDefault();
    setSaving(true);
    setError(null);
    setSuccess(false);
    try {
      const data = company ? await updateCompany(company.id, form) : await createCompany(form);
      setCompany(data);
      setSuccess(true);
    } catch (err) {
      setError(getErrorMessage(err, 'Failed to save company profile.'));
    } finally {
      setSaving(false);
    }
  }

  if (loading) {
    return (
      <div className="flex justify-center py-16">
        <Spinner />
      </div>
    );
  }

  return (
    <div className="space-y-6">
      <div>
        <h2 className="text-2xl font-bold text-slate-900">Company profile</h2>
        <p className="text-sm text-slate-500">
          {company ? 'Update your company details.' : 'Create your company profile before posting jobs.'}
        </p>
      </div>

      {error && <Alert variant="error">{error}</Alert>}
      {success && <Alert variant="success">Company profile saved.</Alert>}

      <Card>
        <form onSubmit={handleSubmit} className="grid grid-cols-1 gap-4 sm:grid-cols-2">
          <Input
            id="companyName"
            name="companyName"
            label="Company name"
            required
            value={form.companyName}
            onChange={handleChange}
            className="sm:col-span-2"
          />
          <Input id="website" name="website" label="Website" value={form.website} onChange={handleChange} />
          <Input id="location" name="location" label="Location" value={form.location} onChange={handleChange} />
          <Input
            id="logo"
            name="logo"
            label="Logo URL"
            value={form.logo}
            onChange={handleChange}
            className="sm:col-span-2"
          />
          <div className="sm:col-span-2">
            <label htmlFor="description" className="mb-1 block text-sm font-medium text-slate-700">
              Description
            </label>
            <textarea
              id="description"
              name="description"
              rows={4}
              value={form.description}
              onChange={handleChange}
              className="block w-full rounded-md border-0 px-3 py-2 text-slate-900 shadow-sm ring-1 ring-inset ring-slate-300 focus:ring-2 focus:ring-inset focus:ring-indigo-600 sm:text-sm"
            />
          </div>
          <div className="sm:col-span-2">
            <Button type="submit" loading={saving}>
              {company ? 'Save changes' : 'Create company'}
            </Button>
          </div>
        </form>
      </Card>
    </div>
  );
}
